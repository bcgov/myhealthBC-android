package ca.bc.gov.data.di

import ca.bc.gov.data.datasource.remote.BannerRemoteDataSource
import ca.bc.gov.data.datasource.remote.CommentRemoteDataSource
import ca.bc.gov.data.datasource.remote.DependentsRemoteDataSource
import ca.bc.gov.data.datasource.remote.ImmunizationRemoteDataSource
import ca.bc.gov.data.datasource.remote.LaboratoryRemoteDataSource
import ca.bc.gov.data.datasource.remote.MedicationRemoteDataSource
import ca.bc.gov.data.datasource.remote.NotificationRemoteDataSource
import ca.bc.gov.data.datasource.remote.PatientRemoteDataSource
import ca.bc.gov.data.datasource.remote.PatientServicesRemoteDataSource
import ca.bc.gov.data.datasource.remote.TermsOfServiceRemoteDataSource
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPublicApi
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
    fun providesImmunizationRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi,
        healthGatewayPublicApi: HealthGatewayPublicApi
    ) =
        ImmunizationRemoteDataSource(healthGatewayPrivateApi, healthGatewayPublicApi)

    @Provides
    @Singleton
    fun providesLaboratoryRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi,
    ) = LaboratoryRemoteDataSource(healthGatewayPrivateApi)

    @Provides
    @Singleton
    fun providesMedicationRemoteDataSource(healthGatewayPrivateApi: HealthGatewayPrivateApi) =
        MedicationRemoteDataSource(healthGatewayPrivateApi)

    @Provides
    @Singleton
    fun providesPatientRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi
    ) =
        PatientRemoteDataSource(healthGatewayPrivateApi)

    @Provides
    @Singleton
    fun provideCommentRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi
    ) = CommentRemoteDataSource(healthGatewayPrivateApi)

    @Provides
    @Singleton
    fun provideDependentsRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi
    ) = DependentsRemoteDataSource(healthGatewayPrivateApi)

    @Provides
    @Singleton
    fun provideNotificationRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi
    ) = NotificationRemoteDataSource(healthGatewayPrivateApi)

    @Singleton
    @Provides
    fun providesTermsOfServiceRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi
    ): TermsOfServiceRemoteDataSource = TermsOfServiceRemoteDataSource(healthGatewayPrivateApi)

    @Provides
    @Singleton
    fun provideBannerRemoteDataSource(
        healthGatewayPublicApi: HealthGatewayPublicApi
    ) = BannerRemoteDataSource(healthGatewayPublicApi)

    @Provides
    @Singleton
    fun providePatientServicesRemoteDataSource(
        healthGatewayPrivateApi: HealthGatewayPrivateApi
    ): PatientServicesRemoteDataSource = PatientServicesRemoteDataSource(healthGatewayPrivateApi)
}
