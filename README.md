# Android Architecture Blueprints [beta] - MVP + Clean Architecture ＋ Rxjava + Dagger2

### Summary
This sample stands on the principles of [Clean Architecture](https://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html).

It's based on the [MVP sample](https://github.com/googlesamples/android-architecture/tree/todo-mvp), adding a domain layer between the presentation layer and repositories, splitting the app in three layers:

<img src="https://github.com/googlesamples/android-architecture/wiki/images/mvp-clean.png" alt="Diagram"/>

* **MVP**: Model View Presenter pattern from the base sample.
* **Domain**: Holds all business logic. The domain layer starts with classes named *use cases* or *interactors* used by the application presenters. These *use cases* represent all the possible actions a developer can perform from the presentation layer. 
* **Repository**: Repository pattern from the base sample.  

### Key concepts
*Use cases* define the operations that the app needs. This increases readability since the names of the classes make the purpose obvious (see [tasks/domain/usecase/](https://github.com/googlesamples/android-architecture/tree/todo-mvp-clean/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/tasks/domain/usecase)).

*Use cases* are good for operation reuse over our domain code. [`CompleteTask`] (https://github.com/googlesamples/android-architecture/blob/todo-mvp-clean/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/tasks/domain/usecase/CompleteTask.java) is a good example of this as it's used from both the [`TaskDetailPresenter`](https://github.com/googlesamples/android-architecture/blob/todo-mvp-clean/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/taskdetail/TaskDetailPresenter.java) and the [`TasksPresenter`](https://github.com/googlesamples/android-architecture/blob/todo-mvp-clean/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/tasks/TasksPresenter.java).

The execution of these *use cases* is done in a background thread using the [command pattern](http://www.oodesign.com/command-pattern.html). The domain layer is completely decoupled from the Android SDK or other third party libraries.

### Issues/notes
We are using asynchronous repositories, but there's no need to do this any more because use cases execute off the main thread. This is kept to maintain the sample as similar as possible to the original one.

We recommend using different models for View, domain and API layers, but in this case all models are immutable so there's no need to duplicate them. If View models contained any Android-related fields, we would use two models, one for domain and other for View and a mapper class that converts between them.

Callbacks have an `onError` method that in a real app should contain information about the problem.

### Dependencies

RxJava & Dagger2

## Features

### Complexity - understandability

#### Use of architectural frameworks/libraries/tools: 

None

#### Conceptual complexity 

Medium-Low, it's an MVP approach with a new layer that handles domain logic.
Developers need to be familiar with RxJava, which is not trivial.

#### Testability

Super high. 
With this approach, all domain code is tested with unit tests. This can be extended with integration tests, that cover from Use Cases to the boundaries of the view and repository.
Use of Dagger2 improves flexibility in local integration tests and UI tests. Components can be replaced by doubles very easily and test different scenarios.

### Code metrics

Adding a domain layer produces more classes and Java code.

```
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Java                            77           1378           1733           4439(3450 in MVP)
XML                             34             97            337            602
-------------------------------------------------------------------------------
SUM:                           111           1475           2070           5041
-------------------------------------------------------------------------------

```
### Maintainability

#### Ease of amending or adding a feature 
High.

#### Learning cost
Medium, as RxJava is not trivial and developers need to be aware of how Dagger2 works, although the setup of new features should look very similar to existing ones.




