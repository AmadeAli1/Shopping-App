package com.amade.dev.shoppingapp.service.publisher

import com.amade.dev.shoppingapp.cloud.StorageService
import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.model.publisher.CompanyAddress
import com.amade.dev.shoppingapp.model.publisher.CompanyImage
import com.amade.dev.shoppingapp.model.publisher.dto.CompanyDTO
import com.amade.dev.shoppingapp.repository.publisher.CompanyAddressRepository
import com.amade.dev.shoppingapp.repository.publisher.CompanyImageRepository
import com.amade.dev.shoppingapp.repository.publisher.CompanyRepository
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

    suspend fun save(body: Company, logoImage: FilePart): CompanyDTO {
        var company: Company? = null
        var logoPath: String? = null
        try {
            return if (!existsByEmail(body.email)) {
                val encryptedPassword = encode(body.password)
                company = companyRepository.save(body.copy(password = encryptedPassword))
                val subDirectory = "${company.uid}/${company.companyName}".trim()
                logoPath = service.save(logoImage, subDirectory)
                val linkDownload = environment["public.download.link"]!! + logoPath
                company = companyRepository.save(company.copy(logoUrl = linkDownload))
                CompanyDTO(company)
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

    suspend fun saveCompanyImage(body: CompanyImage, companyName: String, list: List<FilePart>): List<CompanyImage> {
        if (list.isEmpty()) throw ApiException("Required equal or great than one")
        try {
            return list.map {
                val pathId = service.save(it, "${body.companyId}/$companyName")
                val linkDownload = environment["public.download.link"]!! + companyName
                companyImageRepository.save(entity = body.copy(path = pathId, imageUrl = linkDownload))
            }
        } catch (e: Exception) {
            throw ApiException(e.message)
        }

    }

    suspend fun deleteImage(filePath: String): Boolean {
        return service.delete(filePath)
    }

    suspend fun deleteCompany(email: String, password: String): String {
        val company = companyRepository.findByEmail(email)
        if (company != null) {
            try {
                if (decode(company.password, password)) {
                    if (companyRepository.deleteByUid(company.uid!!) != 0) {
                        return "Your account has been deleted!"
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


        return CompanyDTO(Company())
    }

    private suspend fun encode(password: String) = passwordEncoder.encode(password)

    private suspend fun decode(encryptedPassword: String, password: String): Boolean {
        return passwordEncoder.matches(password, encryptedPassword)
    }

    private suspend fun existsByEmail(email: String): Boolean {
        return companyRepository.existsByEmail(email)
    }

}