package com.amade.dev.shoppingapp.service.publisher

import com.amade.dev.shoppingapp.cloud.StorageService
import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.utils.ApiResponse
import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.model.publisher.CompanyAddress
import com.amade.dev.shoppingapp.model.publisher.CompanyImage
import com.amade.dev.shoppingapp.model.publisher.dto.CompanyDTO
import com.amade.dev.shoppingapp.repository.publisher.CompanyAddressRepository
import com.amade.dev.shoppingapp.repository.publisher.CompanyImageRepository
import com.amade.dev.shoppingapp.repository.publisher.CompanyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val service: StorageService,
    private val passwordEncoder: PasswordEncoder,
    private val companyRepository: CompanyRepository,
    private val companyAddressRepository: CompanyAddressRepository,
    private val companyImageRepository: CompanyImageRepository,
    private val environment: Environment,
) {

    suspend fun save(companyBody: Company, addressBody: CompanyAddress, logoImage: FilePart): CompanyDTO {
        var company: Company? = null
        var logoPath: String? = null
        try {
            return if (!existsByEmail(companyBody.email)) {
                val encryptedPassword = encode(companyBody.password)
                company = companyRepository.save(companyBody.copy(password = encryptedPassword))
                val subDirectory = "company/${company.uid}".trim()
                logoPath = service.save(logoImage, subDirectory)
                val linkDownload = environment["public.download.link"]!! + logoPath
                company = companyRepository.save(company.copy(logoUrl = linkDownload))
                val address = saveAddress(addressBody.copy(companyId = company.uid))
                CompanyDTO(company, address)
            } else {
                throw ApiException("This email already in use!")
            }
        } catch (e: Exception) {
            if (company != null) {
                companyRepository.delete(company)
                if (logoPath != null) service.delete(logoPath)
            }
            throw ApiException(e.message)
        }
    }

    suspend fun saveAddress(address: CompanyAddress): CompanyAddress {
        return companyAddressRepository.save(address)
    }

    suspend fun saveCompanyImages(body: CompanyImage, images: List<FilePart>): List<CompanyImage> {
        if (images.isEmpty()) throw ApiException("Required equal or great than one")
        val uploadSuccess = mutableListOf<CompanyImage>()
        val subDirectory = "company/${body.companyId}"
        try {
            return images.map {
                val pathId = service.save(it, subDirectory)
                val linkDownload = environment["public.download.link"]!! + pathId
                val saved = companyImageRepository.save(entity = body.copy(path = pathId, imageUrl = linkDownload))
                uploadSuccess.add(saved)
                saved
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Default) {
                uploadSuccess.forEach {
                    companyImageRepository.delete(it)
                    service.delete(it.path!!)
                }
            }
            throw ApiException(e.message)
        }
    }

    suspend fun deleteCompanyImage(filePath: String): ApiResponse<Boolean> {
        val deleteImageByPath = companyImageRepository.deleteImageByPath(filePath)
        if (deleteImageByPath != 0) {
            return ApiResponse(service.delete(filePath))
        }
        throw ApiException("Image not removed!!")
    }

    suspend fun deleteCompany(email: String, password: String): ApiResponse<String> {
        val company = companyRepository.findByEmail(email)
        if (company != null) {
            try {
                if (decode(company.password, password)) {
                    if (companyRepository.deleteByUid(company.uid!!) != 0) {
                        return ApiResponse("Your account has been deleted!")
                    }
                    throw ApiException("An error occurred when trying delete your account")
                }
            } catch (e: Exception) {
                throw ApiException(e.message)
            }
        }
        throw ApiException("Account not found!")
    }

    suspend fun login(email: String, password: String): CompanyDTO {
        val view = companyRepository.getById(email)
        if (view != null) {
            try {
                if (decode(view.password, password)) {
                    val images = companyImageRepository.getAllByCompanyId(view.uid!!)
                    return view.toCompanyDTO().copy(images = images.toList())
                }
                throw ApiException("Invalid password")
            } catch (e: Exception) {
                throw ApiException(e.message)
            }
        }
        throw ApiException("This account not exists! try another data")
    }

    private suspend fun encode(password: String) = passwordEncoder.encode(password)

    private suspend fun decode(encryptedPassword: String, password: String): Boolean {
        return passwordEncoder.matches(password, encryptedPassword)
    }

    private suspend fun existsByEmail(email: String): Boolean {
        return companyRepository.existsByEmail(email)
    }


}