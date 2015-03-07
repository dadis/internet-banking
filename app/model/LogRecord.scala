package sk.dadizzz.accounting.web.models

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.DB
import sk.dadizzz.accounting.web.models.PayRecord

/**
 * Created by DAVID on 1.2.2015.
 */
case class LogRecord(fileName:String, numberOfRecords:Int, status:String, uploadDate:DateTime) {

  def save() = {
    DB.withConnection { implicit connection =>
      SQL("insert into FileLog(fileName,numberOfRecords,status,uploadDate)" +
        "values({fileName},{numberOfRecords},{status},{uploadDate})")
        .on("fileName"->fileName,
          "numberOfRecords"->numberOfRecords,
          "status"->status,
          "uploadDate"->uploadDate)
        .executeInsert()
    }
  }
}

object LogRecord {

  val mapping = {
    get[String]("fileName") ~
      get[Int]("numberOfRecords") ~
      get[String]("status") ~
      get[DateTime]("uploadDate") map {
      case fileName~numberOfRecords~status~uploadDate=>
        LogRecord(fileName,numberOfRecords,status,uploadDate)
    }
  }

  def all()={DB.withConnection { implicit c =>
    SQL("Select * from FileLog").as( mapping *)
  }}
}


