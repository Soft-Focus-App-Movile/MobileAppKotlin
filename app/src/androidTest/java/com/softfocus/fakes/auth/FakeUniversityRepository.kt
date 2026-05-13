package com.softfocus.fakes.auth

import com.softfocus.core.data.repositories.UniversityInfo
import com.softfocus.core.data.repositories.UniversityRepository
import io.mockk.coEvery
import io.mockk.mockk

/**
 * Factory que crea un UniversityRepository mockeado con comportamiento controlado.
 *
 * UniversityRepository no tiene interfaz y es una clase final,
 * así que no se puede heredar. Usamos MockK para crear un mock
 * y configurar su comportamiento desde los tests.
 */
object FakeUniversityRepository {

    fun create(
        searchResult: Result<List<UniversityInfo>> = Result.success(defaultUniversities())
    ): UniversityRepository {
        val mock = mockk<UniversityRepository>(relaxed = true)
        coEvery { mock.searchUniversities(any()) } answers {
            val query = firstArg<String>()
            if (query.length < 2) Result.success(emptyList())
            else searchResult
        }
        return mock
    }

    fun defaultUniversities() = listOf(
        UniversityInfo("Universidad Nacional Mayor de San Marcos", "Lima"),
        UniversityInfo("Pontificia Universidad Católica del Perú", "Lima"),
        UniversityInfo("Universidad de Lima", "Lima")
    )
}
