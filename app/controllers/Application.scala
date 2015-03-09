package sk.dadizzz.accounting.web.controllers


import org.joda.time.DateTime
import play.api.mvc._
import play.api.libs.json.Json
import sk.dadizzz.accounting.web.models.{PayRecord, Month, LogRecord}
import sk.dadizzz.accounting.web.services.IBParserService


object Application extends Controller {

  //  val august = new Month("AUGUST","g:\\downloads-D\\vypis_SLSP\\08_AUGUST\\august.txt")


  def index = Action {


    implicit request =>
      Ok(views.html.index(Month.getListOfMonths))
  }

  def seeAll = Action {

    val all = Month.findAll


    Ok(views.html.all(all))

    //TODO continute here
  }

  def seeMonth(month:Int,year:Int) = Action {

    val monthExist:Boolean = Month.checkMonth(month,year);

    val data = {
      if (!monthExist || (month == 0 && year == 0)) {
        val l =Month.findLast
        Month.findDataForMonth(l._1.toInt, l._2.toInt)
      }
      else {
        println("month:"+month)
        println("year:"+year)
        Month.findDataForMonth(month, year)
      }
    }
    Ok(views.html.month(new Month(data)))

  }

  def category = Action {
    Ok(views.html.void("a"))
  }

  def saved = Action {
    Ok(views.html.void("x"))
  }

  def uploadForm = Action {
    implicit request =>
      Ok(views.html.upload())
  }

  def log = Action{
    implicit request =>
      Ok(views.html.log(LogRecord.all()))
  }


  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("month").map { (file) =>

      val month=new Month(IBParserService.parseCSVtoList(file.ref.file))

      var status = "OK";
      try {
        Month.saveMonth(month)
      }catch{
        case ex => status="ALREADYLOADED"
      }

      val logRecord = new LogRecord(month.name+""+month.year,month.data.size,status,new DateTime())
      logRecord.save()

      Redirect(routes.Application.index).flashing( "success" -> "File uploaded")
    }.getOrElse {

      Redirect(routes.Application.index).flashing( "error" -> "Missing file"
      )
    }
  }

  def balanceAsJson = Action {
    Ok(Json.toJson(Month.findAll.map {
      p => p.totalSaved
    }
    ))
  }

}