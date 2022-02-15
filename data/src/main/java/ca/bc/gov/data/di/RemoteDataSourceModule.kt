package ca.bc.gov.data.di

import ca.bc.gov.data.ImmunizationRemoteDataSource
import ca.bc.gov.data.LaboratoryRemoteDataSource
import ca.bc.gov.data.MedicationRemoteDataSource
import ca.bc.gov.data.PatientRemoteDataSource
import ca.bc.gov.data.remote.ImmunizationApi
import ca.bc.gov.data.remote.LaboratoryApi
import ca.bc.gov.data.remote.MedicationApi
import ca.bc.gov.data.remote.PatientApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author Pinakin Kansara
 */
@InstallIn(SingletonComponent::class)
@Module
class RemoteDataSourceModule {

    @Provides
    @Singleton
    fun providesImmunizationRemoteDataSource(immunizationApi: ImmunizationApi) =
        ImmunizationRemoteDataSource(immunizationApi)

    @Provides
    @Singleton
    fun providesLaboratoryRemoteDataSource(laboratoryApi: LaboratoryApi) =
        LaboratoryRemoteDataSource(laboratoryApi)

    @Provides
    @Singleton
    fun providesMedicationRemoteDataSource(medicationApi: MedicationApi) =
        MedicationRemoteDataSource(medicationApi)

    @Provides
    @Singleton
    fun providesPatientRemoteDataSource(patientApi: PatientApi) =
        PatientRemoteDataSource(patientApi)
}
