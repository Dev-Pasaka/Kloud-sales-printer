import data.remote.request.GetReceiptsReq
import data.repository.GetReceiptsRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import gui.ava.html.image.generator.HtmlImageGenerator
import org.xhtmlrenderer.simple.PDFRenderer
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.apache.pdfbox.pdmodel.PDDocument
import org.xhtmlrenderer.pdf.ITextRenderer
import org.xhtmlrenderer.simple.Graphics2DRenderer
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import org.apache.pdfbox.rendering.PDFRenderer as PdfBoxRenderer
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


/*fun generateImage(html: String) {
    val imageGenerator = HtmlImageGenerator()

    // Load HTML
    imageGenerator.loadHtml(html)

    // Save as image with specified width and height
    val imageWidth = 640 // Width in pixels for 80 mm at 203 DPI
    val imageHeight = imageGenerator.size.height // Maintain the aspect ratio

    // Set the size and save the image
    imageGenerator.bufferedImage.getScaledInstance(imageWidth, imageHeight, java.awt.Image.SCALE_SMOOTH)
    imageGenerator.saveAsImage("testing.png")
}*/




suspend fun main() {
    val htmlContent = GetReceiptsRepositoryImpl().getReceipts(GetReceiptsReq()).first()
    val formattedContent = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(htmlContent)
    //generateImage(formattedContent,1000, )
    //ReceiptRepositoryImpl().mergeImagesVertically("testing.png","qr_code_image.png",  "./receipts/merged_image.png")
    ReceiptRepositoryImpl().printPNGImage("./receipts/merged_image.png")
}