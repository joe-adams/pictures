package com.github.joe.adams.steps.normalizedata

import com.github.joe.adams.Util
import com.github.joe.adams.db.DbService
import org.scalatest.FunSuite

class PopulateDataTest extends FunSuite {

  test("test populate") {
    val dbService = DbService()
    Util.sync(dbService.truncate)
    val exifRaw = dbService.exifRaw("filename", "directory", 5, Some("tagname"), "desc")
    dbService.addInitialJpegs(Seq(exifRaw.filename))
    dbService.addExifsRaw(Seq(exifRaw))
    Util.sync(() => PopulateData())
    Util.sync(dbService.truncate)
  }


}
