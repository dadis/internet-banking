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

    val m:Month = {
      if (month == 0 && year == 0) {
        val l =Month.findLast
        Month.findAllForMonth(l._1, l._2)
      }
      else {
        Month.findAllForMonth(month, year)
      }
    }

    if(m.data.isEmpty) {
      Redirect(routes.Application.index())
    }
    else {

      println(m.previousMonth)
      println(m.nextMonth)
      Ok(views.html.month(m))
    }
  }

//  def seeLastMonth = Action {
//
//    val lastMonth = Month.findLast
//
//    val m = Month.findAllForMonth(lastMonth._1,lastMonth._2)
//
//    if(m.data.isEmpty) {
//      Redirect(routes.Application.index())
//    }
//    else {
//      Ok(views.html.month(m,m.previousMonth,m.nextMonth))
//    }
//  }


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

      Month.saveMonth(month)

      val logRecord = new LogRecord(month.name+""+month.year,month.data.size,"OK",new DateTime())
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