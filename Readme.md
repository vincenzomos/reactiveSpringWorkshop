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
 
 * Reactor is targeting Java 8 and providing an Rx-conforming API
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
There is a unittest in  [nl.sogeti.reactivespring.basics] named SubscribeDemo with a method to get the data from the Flux. But the test fails.
In order to make the data really start flowing you need to subscribe on the Flux. Try to make the test work.

Once you got the test working you can see the flow of events in the logging. 

Now let’s go through the sequence that we have logged one by one:

1. onSubscribe() – This is called when we subscribe to our stream
2. request(unbounded) – When we call subscribe, behind the scenes we are creating a Subscription. This subscription requests elements from the stream. In this case, it defaults to unbounded, meaning it requests every single element available
3. onNext() – This is called on every single element
4. onComplete() – This is called last, after receiving the last element. There’s actually a onError() as well, which would be called if there is an exception, but in this case, there isn’t    

### Practicing with Flux and Mono
 In the project reactivespring there is a package [nl.sogeti.reactivespring.basics]. In here are a couple of classes prefix with Part<number>...
 All these are some practice classes to implement some constructs for Monos and Fluxes.
 (Resource is [https://github.com/reactor/lite-rx-api-hands-on.git])
 
### BackPressure
Backpressure is one of the things that is considered a valuable asset when streaming data. It gives the subscriber the possibillity 
to read data at it's own pace. 
 - request(n)
 - write
 - flush imediately so items are visual instanly
 - repeat
 
### Reactive data repository

"application/json" results in a finite collection
Browsers only can consume a stream by producing Server Sent events.

### Cold Stream vs Hot Stream

