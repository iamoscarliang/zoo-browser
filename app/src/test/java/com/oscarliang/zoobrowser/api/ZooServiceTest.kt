package com.oscarliang.zoobrowser.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class ZooServiceTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: ZooService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZooService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetAreas() = runTest {
        enqueueResponse("areas.json")
        val response = service.getAreas()

        val request = mockWebServer.takeRequest()
        assertEquals(
            request.path,
            "/5a0e5fbb-72f8-41c6-908e-2fb25eff9b8a?scope=resourceAquire"
        )

        assertNotNull(response)
        assertEquals(response.result.results.size, 16)

        val area1 = response.result.results[0]
        assertEquals(area1.id, 1)
        assertEquals(area1.name, "臺灣動物區")
        assertEquals(area1.category, "戶外區")
        assertEquals(area1.url, "https://youtu.be/QIUbzZ-jX_Y")

        val area2 = response.result.results[1]
        assertEquals(area2.id, 2)
        assertEquals(area2.name, "兒童動物區")
        assertEquals(area2.category, "戶外區")
        assertEquals(area2.url, "https://youtu.be/CC4dlmRIVls")
    }

    @Test
    fun testSearchAnimals() = runTest {
        enqueueResponse("animals.json")
        val response = service.searchAnimals(
            "animal",
            10
        )

        val request = mockWebServer.takeRequest()
        assertEquals(
            request.path,
            "/a3e2b221-75e0-45c1-8f97-75acbd43d613?scope=resourceAquire&q=animal&limit=10"
        )

        assertNotNull(response)
        assertEquals(response.result.results.size, 10)

        val animal1 = response.result.results[0]
        assertEquals(animal1.id, 1)
        assertEquals(animal1.name, "大貓熊")
        assertEquals(animal1.location, "新光特展館（大貓熊館）")
        assertEquals(
            animal1.imageUrl,
            "http://www.zoo.gov.tw/iTAP/03_Animals/PandaHouse/Panda_Pic01.jpg"
        )

        val animal2 = response.result.results[1]
        assertEquals(animal2.id, 2)
        assertEquals(animal2.name, "國王企鵝")
        assertEquals(animal2.location, "企鵝館")
        assertEquals(
            animal2.imageUrl,
            "http://www.zoo.gov.tw/iTAP/03_Animals/PenguinHouse/KingPenguin/KingPenguin_Pic01.jpg"
        )
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }

}