package ca.bc.gov.repository.di

import android.content.Context
import ca.bc.gov.repository.utils.UriToImage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * @author Pinakin Kandara
 */
@InstallIn(SingletonComponent::class)
@Module
class UtilityModule {

    @Provides
    fun providesUriToImage(@ApplicationContext context: Context) = UriToImage(context)

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
