package data.local.entries

import domain.model.PrintingStatus
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId




/*fun PrintingLogsObj.toPrintingLogs(): PrintingLogs{
    return PrintingLogs(
        receiptNumber = receiptNumber,
        user = user,
        date = date,
        time = time,
        status = status?.let { PrintingStatus.valueOf(it.name) } ?: PrintingStatus.PRINTING,
        action = action
    )
}*/

