## Goforer Android Advanced App Architecture 1.0 Blueprint
<img src="https://github.com/Lukoh/CleanArchitecture_Example/blob/master/Architecture.png" alt="Log-in Demo" width="880" />

During Google I/O 2017, Android Team announced [guidelines](https://developer.android.com/topic/libraries/architecture/index.html) for architecture of Android app. So I applied it to this project. Please refer to the description below:
The Android framework provides a lot of flexibility in deciding how to organize and architect all Android Apps. While this freedom is very valuable, it can also lead to apps with large classes, inconsistent naming schemes, as well as mismatching or missing architectures. These types of issues can make testing, maintaining and extending your apps difficult.
The Goforer Android Advanced App Architecture 1.0 Blueprints project demonstrates strategies to help solve or avoid these common problems. 
You can use all source code in this project as a learning reference, or as a starting point for creating Android apps. The focus of this project is on demonstrating how to structure your code, design your architecture, and the eventual impact of adopting these patterns on testing and maintaining Android app. You can use the techniques demonstrated here in many different ways to build apps. Your own particular priorities will impact how you implement the concepts in these projects, so you should not consider these samples to be canonical examples. To ensure the focus in kept on the aims described above, the app uses a simple UI.
 
 
## Summary
Goforer Advanced Android Architecture consist of Presentation layer, Domain layer and Repository layer. And new latest technologies, Clean Architecture + Dagger 2.11 + MVVM design pattern + LiveData + ROOM Tech, were applied into Advanced Android App Architecture. And [RecyclreFragment](https://github.com/Lukoh/AndoridAppArch/blob/master/app/src/main/java/com/goforer/base/presentation/view/fragment/RecyclerFragment.java) Ver 2.0 has been updated. The many advanced fuctions already were applied into [RecyclreFragment](https://github.com/Lukoh/AndoridAppArch/blob/master/app/src/main/java/com/goforer/base/presentation/view/fragment/RecyclerFragment.java). These stuff make Android Apps to be extended being more competitive power and help them to maintain consistency.

## Components Demonstrated
- [Dagger 2.11](https://google.github.io/dagger/)
- [Room](https://developer.android.com/topic/libraries/architecture/room.html)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata.html)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel.html)

## Goforer Advanced Android App Architecture
<img src="https://github.com/Lukoh/AppArch/blob/master/GoforerAppAchitecture.png" alt="Log-in Demo" width="880" />

### Prerequisites

- Android Studio 3.0 Canary 5
- Android Device with USB Debugging Enabled

# License
```
Copyright 2017 Lukoh Nam, goForer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


 
