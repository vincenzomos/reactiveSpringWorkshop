# Reactor Core and Spring 5  workshop

This workshop is set up as  an introduction to work with 
Reactive Streams in  Spring 5. From the Background of non-reactive Java development, going reactive can be quite a steep learning curve.
With this workshop we'll take some steps through Reactor and Spring 5 until we have an idea of how to 
compose reactive Code. Maybe some concepts are difficult to grasp at first. But you should be able to just go along. 
You can allways later on dive a bit more in the theory. Links to some useful resources are provided.   

## Background information
 The [Reactive Streams specification](http://www.reactive-streams.org/) is the groundwork for the Reactor library.
 
 Essentially, Reactive Streams is a specification for asynchronous stream processing.
 
 In other words, a system where lots of events are being produced and consumed asynchronously. 
 Think about a stream of thousands of stock updates per second coming into a financial application, and for it to have to respond 
 to those updates in a timely manner.
 
 * Reactor is targeting Java 8 and implments The Reactive Streams specification
 * It uses the same approach and philosophy as RxJava despite some API differences
 * Reactor is a core dependency in the reactive programming model support of Spring Framework 5.
 
 ## Content of the workshop
 * How to work with Flux and Mono
 * Building Reactive applications with Spring 5
 
 ### Flux and Mono
 In order for an application to be reactive, the first thing it must be able to 
 do is to produce a stream of data. This could be something like the stock update
 example that we gave earlier. Without this data, we wouldn’t have anything to 
 react to, which is why this is a logical first step. Reactive Core gives us 
 two data types that enable us to do this.  
  
 ### Flux
 The first way of doing this is with a Flux.  It’s a stream which can emit 0..n elements. Let’s try creating a simple one:
 
  ``
  Flux<String> just = Flux.just("1", "2", "3");
  `` 
  
  In this case, we have a static stream of three elements.
  
  you can also create a Flux quite easily from a Collection, Array or Stream. 
  example for a Collection:
  
  ``
  Flux<String> fluxFromCollection = Flux.fromIterable(Arrays.asList("foo", "bar"));
  `` 
      
  ### Mono
  The second way of doing this is with a Mono, which is a stream of 0..1 elements. Let’s try instantiating one:
  
  ``
  Mono<String> just = Mono.just("foo");
  ``
  
  This looks and behaves almost exactly the same as the Flux, only this time we are limited to no more than one element.
 
 ### Why Not Just Flux?
 Before experimenting further, it’s worth highlighting why we have these two data types.
 
 First, it should be noted that both a Flux and Mono are implementations of the Reactive Streams Publisher interface. Both classes are compliant with the specification, and we could use this interface in their place:
 
 ``
 Publisher<String> just = Mono.just("foo");
 ``
 
 But really, knowing this cardinality is useful. This is because a few operations only make sense for one of the two types, and because it can be more expressive (imagine findOne() in a repository).
 

### Getting Data from a Flux
In the module reactivespring there is a unittest in  [nl.sogeti.reactivespring.basics] named SubscribeDemo with a method to get the data from the Flux. But the test fails.
In order to make the data really start flowing you need to subscribe on the Flux. Try to make the test work.

Once you got the test working you can see the flow of events in the logging. 

Now let’s go through the sequence that we have logged one by one:

1. onSubscribe() – This is called when we subscribe to our stream
2. request(unbounded) – When we call subscribe, behind the scenes we are creating a Subscription. This subscription requests elements from the stream. In this case, it defaults to unbounded, meaning it requests every single element available
3. onNext() – This is called on every single element
4. onComplete() – This is called last, after receiving the last element. There’s actually a onError() as well, which would be called if there is an exception, but in this case, there isn’t    


### BackPressure
The next thing we should consider is backpressure. In our example, the subscriber is telling the producer to push every single element at once.
This could end up becoming overwhelming for the subscriber, consuming all of its resources.
Backpressure is when a downstream can tell an upstream to send it fewer data in order to prevent it from being overwhelmed.

In the SubscribeDemo test class there are also showing the principles of backpressure.
The test demoSubcriberImpl will just read all items at once, while the other method demoSubcriberWithAdaptedBackpressure instructs the publisher to send 2 items
at a time.

We can modify our Subscriber implementation to apply backpressure. Let’s tell the upstream to only send two elements at a time by using request():
to read data at it's own pace. 
 
 - request(n)
 - write
 - flush imediately so items are visual instanly
 - repeat
 
### Practicing with Flux and Mono
 In the project reactivespring there is a package [nl.sogeti.reactivespring.basics]. In here are a couple of classes prefixed with Part<number>... can be found.
 All these are some practice classes to implement some constructs for Monos and Fluxes. In here you'll also find some examples where Stepverifier is used. StepVerifier is a nice convenience class that makes it possible to verify how the stream you produce will behave. I made a selection of practices from the following source [https://github.com/reactor/lite-rx-api-hands-on.git])


### A reactive restservice

Spring WebFlux comes in two flavors of web applications: annotation based and functional.

The cryptodataretrieval module contains an example of a Spring restService in the class : RequestMappingStyleBitcoinDataController. Take a look at the class.
Actually this should look quite familiar if you have worked with Spring MVC RequestMappings. Now start the application.
If you're IDE is smart enough you can by just starting the class : BitcoinDataProvidingApplication. By just selecting the class and start.
You can also start from the commandline by running the following from the cryptodataretrieval folder:

``
mvn spring-boot:run
``

If everything started correctly you should be able to look at a stream of data in the following way:

``
curl http://localhost:8085/streamData
``

In a browser it can work as well. I did with Chrome and that went fine. The thing is the browser needs to know how to deal with Server Sent events.


### Create your first HandlerFunction + RouterFunction
Now you have seen how a service is implemented using the annotations. For a change it might be nice to implement a service the functional-reactive way.


Incoming HTTP requests are handled by a HandlerFunction, which is essentially a function that takes a ServerRequest and returns a Mono<ServerResponse>. The annotation counterpart to a handler function would be a Controller method.
But how those incoming requests are routed to the right handler?

We’re using a RouterFunction, which is a function that takes a ServerRequest, and returns a Mono<HandlerFunction>. If a request matches a particular route, a handler function is returned; otherwise it returns an empty Mono. The RouterFunction has a similar purpose as the @RequestMapping annotation in @Controller classes.

There is already a class named FunctionalStyleBitcoinDataConfiguration.
In this class we are going to create the logic needed to create the reactive service.

So first you implement the HandlerFunction to return the bitcoindatastream. You should use the `BitcoinDataService.getBitcoinData` to retrieve the data.

To route requests to that handler, you need to expose a RouterFunction to Spring Boot. You can do this by creating a @Bean of type RouterFunction<ServerResponse>.

Modify that class so that GET requests to "/streamDataFunctional" are routed to the handler you just implemented.

*Some tips*
- The content type "application/json" results in a finite collection
- Browsers only can consume a stream by producing Server Sent events. (`MediaType.TEXT_EVENT_STREAM`)
- More info on [the Spring WebFlux.fn reference documentation](http://docs.spring.io/spring-framework/docs/5.0.3.RELEASE/spring-framework-reference/web.html#web-reactive-server-functional)

### Cold Stream vs Hot Stream
We implemented this service now but maybe you've noticed that the data is just showing the same data on each request all over again.
This is because the data we return are is static, fixed length streams which are easy to deal with.
A more realistic use case for reactive might be something that happens infinitely. In this example bitcoin price changes will never stop off course.
These types of streams are called hot streams, as they are always running and can be subscribed to at any point in time, missing the start of the data.

One way to create a hot stream is by converting a cold stream into one. Let’s create a Flux that starts when we startup the application and keeps on s
streaming the bitcoinprices without starting over.
This would simulate an infinite stream of data coming from an external resource:

```java
ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
    while(true) {
        fluxSink.next(System.currentTimeMillis());
    }
})
  .publish();
```

By calling publish() we are given a ConnectableFlux. This means that calling subscribe() won’t cause it start emitting, allowing us to add multiple subscriptions:

Now try to add a ConnectableFlux of data in the BitcoinDataService and create another endpoint for that named "/hotStreamData".



