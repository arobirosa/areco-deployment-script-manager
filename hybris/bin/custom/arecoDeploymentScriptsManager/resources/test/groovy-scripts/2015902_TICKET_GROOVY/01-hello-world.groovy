class HelloWorld {
  def name
  def greet() { "Hello ${name}" }
}
def helloWorld = new HelloWorld()
helloWorld.name = "OKa"
println helloWorld.greet()