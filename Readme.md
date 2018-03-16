#Reactor Core and Spring 5  workshop

This workshop is set up as  an introduction to work with 
Reactive Streams in  Spring 5. From the Background of non-reactive Java development, going reactive can be quite a steep learning curve.
With this workshop we'll take some steps through Reactor and Spring 5 until we have an idea of how to 
compose reactive Code. Maybe some concepts are difficult to grasp at first. But you should be able to just go along. 
You can allways later on dive a bit more in the theory. Links to some useful resources are provided.   

##Background information
 The [Reactive Streams specification](http://www.reactive-streams.org/) is the groundwork for the Reactor library.
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
 
 ### Practicing with Flux and Mono
 In the project reactivespring there is a package [nl.sogeti.reactivespring.basics]. In here are a couple of classes prefix with Part<number>...
 All these are some practice classes to implement some constructs for Monos and Fluxes.
 (Resource is [https://github.com/reactor/lite-rx-api-hands-on.git])
 There are a few pieces of code that should get you familiar with working with these constructs.
    
