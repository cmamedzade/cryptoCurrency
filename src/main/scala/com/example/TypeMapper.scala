package com.example

import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}

import java.time.LocalDateTime
import java.util.UUID

object TypeMapper{
  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)
    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _              => throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object LocalDT extends JsonFormat[LocalDateTime] {
    def write(dateTime: LocalDateTime) = JsString(dateTime.toString)
    def read(value: JsValue): LocalDateTime = {
      value match {
        case JsString(date) => LocalDateTime.parse(date)
        case _              => throw DeserializationException("Expected correct date time format")
      }
    }
  }
}

