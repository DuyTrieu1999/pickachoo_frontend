package edu.cutie.lightbackend.helper

import com.google.common.flogger.FluentLogger

interface WithLogger {
  companion object val logger: FluentLogger
    get() = FluentLogger.forEnclosingClass()
}
