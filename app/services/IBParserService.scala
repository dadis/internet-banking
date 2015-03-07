package sk.dadizzz.accounting.web.services

import java.io.File

import anorm.ParameterValue
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import sk.dadizzz.accounting.web.models.PayRecord

import scala.io.Source

object IBParserService {

  def parseCSVtoList(file:File):List[PayRecord] = {

    val dtf:DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")

    Source.fromFile(file, "ISO-8859-1").getLines.map(_.split(";")) map (array => new PayRecord(array(7).toDouble,array(10).toDouble,array(11),"default",dtf.parseDateTime(array(0)))) toList //TODO repair year month

  }






}
