package com.softfocus.features.admin.data.models.response

import com.softfocus.features.admin.domain.models.PsychologistDetail
import com.softfocus.features.admin.domain.models.Specialty

data class PsychologistDetailResponseDto(
    val user: UserDetailDto,
    val psychologistData: PsychologistDataDto
) {
    fun toDomain() = PsychologistDetail(
        id = user.id,
        email = user.email,
        fullName = user.fullName,
        firstName = user.firstName,
        lastName = user.lastName,
        userType = user.userType,
        phone = user.phone,
        profileImageUrl = user.profileImageUrl,
        isActive = user.isActive,
        createdAt = user.createdAt,
        licenseNumber = psychologistData.licenseNumber,
        professionalCollege = psychologistData.professionalCollege,
        collegeRegion = psychologistData.collegeRegion,
        university = psychologistData.university,
        graduationYear = psychologistData.graduationYear,
        specialties = psychologistData.specialties.map { Specialty(name = it) },
        yearsOfExperience = psychologistData.yearsOfExperience,
        isVerified = psychologistData.isVerified,
        verificationDate = psychologistData.verificationDate,
        verifiedBy = psychologistData.verifiedBy,
        verificationNotes = psychologistData.verificationNotes,
        licenseDocumentUrl = psychologistData.documents.licenseDocumentUrl,
        diplomaCertificateUrl = psychologistData.documents.diplomaCertificateUrl,
        identityDocumentUrl = psychologistData.documents.identityDocumentUrl,
        additionalCertificatesUrls = psychologistData.documents.additionalCertificatesUrls
    )
}

data class UserDetailDto(
    val id: String,
    val email: String,
    val fullName: String,
    val firstName: String?,
    val lastName: String?,
    val userType: String,
    val phone: String?,
    val profileImageUrl: String?,
    val isActive: Boolean,
    val createdAt: String
)

data class PsychologistDataDto(
    val licenseNumber: String,
    val professionalCollege: String,
    val collegeRegion: String?,
    val university: String?,
    val graduationYear: Int?,
    val specialties: List<String>,
    val yearsOfExperience: Int,
    val isVerified: Boolean,
    val verificationDate: String?,
    val verifiedBy: String?,
    val verificationNotes: String?,
    val documents: DocumentsDto
)

data class DocumentsDto(
    val licenseDocumentUrl: String?,
    val diplomaCertificateUrl: String?,
    val identityDocumentUrl: String?,
    val additionalCertificatesUrls: List<String>?
)
