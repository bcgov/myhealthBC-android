package ca.bc.gov.repository.di

import ca.bc.gov.repository.scanner.QrScanner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author Pinakin Kansara
 */
@Module
@InstallIn(SingletonComponent::class)
class QrScannerModule {

    @Provides
    fun provideBarcodeScanner() = BarcodeScanning.getClient()

    @Provides
    @Singleton
    fun providesQrScanner(scanner: BarcodeScanner) = QrScanner(scanner)
}
