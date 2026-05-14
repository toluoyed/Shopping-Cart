package com.shoppingcart

final case class AppConfig(target: String)

object GreetingProgram:
  def greeting(config: AppConfig): String =
    s"Hello, ${config.target}"

object Main:
  def program(config: AppConfig): String =
    GreetingProgram.greeting(config)

  @main def run(): Unit =
    Console.println(program(AppConfig("world")))
