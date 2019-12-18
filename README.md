
# StatefulLiveData
[![Release](https://jitpack.io/v/climacell/statefullivedata.svg?style=flat-square)](https://jitpack.io/#climacell/statefullivedata) [![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat-square)](https://android-arsenal.com/api?level=14)

**StatefulLiveData** is a lean, yet powerful tool that harnesses the capabilities of LiveData and enhances them, enabling the observer to distinguish between different states the data can be in, suck as  ***Success***, ***Loading*** and ***Error***.

StatefulLiveData is open-ended, which gives you possibility to create more types of StatefulData, as well as extensions and functionality.
## Quick start guide
### Setup
Adding the dependency to the project using gradle or maven.

#### Gradle setup
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.climacell.statefullivedata:core:1.0.0'
}
```
#### Maven setup
```maven-pom
<!-- <repositories> section of pom.xml -->
<repository>
    <id>jitpack.io</id>
   <url>https://jitpack.io</url>
</repository>

<!-- <dependencies> section of pom.xml -->
<dependency>
    <groupId>com.github.climacell.statefullivedata</groupId>
    <artifactId>core</artifactId>
    <version>1.0.0</version>
</dependency>
```
### Usage
#### Create a StatefulLiveData object:
```kotlin
val myStatefulLiveData: MutableStatefulLiveData<String> = MutableStatefulLiveData<String>()
```
#### Observe:
```kotlin
myStatefulLiveData.observe(lifecycleOwner, Observer { statefulData: StatefulData<String> ->  
	when (statefulData) {  
		is StatefulData.Success -> {  
			showMyData(statefulData.data)  
		}
		is StatefulData.Loading -> {  
			showProgressBar()  
		}
		is StatefulData.Error -> {  
			showError(statefulData.throwable)  
		} 
	}
})
```
#### Update states:
Put success state
```kotlin
myStatefulLiveData.putData("My data String.") 
``` 
Put loading state
```kotlin
myStatefulLiveData.putLoading()
```
Put error state
```kotlin
myStatefulLiveData.putError(IllegalArgumentException())
```
Thats it! You are ready to go! =)

For more awesome capabilities and other super powers check out the other modules that accompany the core module.
Also make sure to look at the kdoc in the library.

## Documentation
Coming soon.
## Modules
There are 4 modules comprising StatefulLiveData:
 - [Core](https://github.com/climacell/StatefulLiveData/tree/master/core) - The essential components that of the StatefulLiveData framework.
 - [Coroutines](https://github.com/climacell/StatefulLiveData/tree/master/coroutines)  - Additional functionalities to further enhance StatefulLiveData.
 - [Google-Tasks](https://github.com/climacell/StatefulLiveData/tree/master/google-tasks) - Provides easy conversions from Tasks to StatefulLiveData.
 - [Retrofit](https://github.com/climacell/StatefulLiveData/tree/master/retrofit) - A Retrofit adapter to convert calls to StatefulLiveData.