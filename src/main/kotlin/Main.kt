import edu.wpi.a1cd.edu.wpi.a1cd.repacker
import edu.wpi.a1cd.unpacker
import java.io.File

fun main() {
    if (!File("StockData.dat").exists())
        repacker()
    unpacker()
}