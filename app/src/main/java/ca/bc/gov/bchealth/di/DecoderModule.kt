package ca.bc.gov.bchealth.di

import android.content.Context
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.readJsonFromAsset
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * [DecoderModule]
 *
 *
 * @author Pinakin Kansara
 */
@Module
@InstallIn(SingletonComponent::class)
class DecoderModule {

    /**
     * This method will provide dependency resolution for shcDecoder.
     *
     * @return SHCDecoder singleton instance
     */
    @Provides
    fun providesSHCDecoder(@ApplicationContext context: Context) =
        SHCDecoder(context.readJsonFromAsset("jwks.json"))
}
