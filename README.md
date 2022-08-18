# adslib

Need add some lines to fix some bugs

      defaultConfig {
          ...
          multiDexEnabled true
      }

      compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
        }
       
 For version 2.x & 3.x need
 
      allprojects {
        repositories {
            google()
            jcenter()
            maven {
            url 'https://unity3ddist.jfrog.io/artifactory/unity-mediation-mvn-prod-local/'
            }
        }
     }
     
 # using unity mediation but old version of google ads
 implementation 'com.github.otakuteam:adslib:v2.4'
 
 # using unity mediation with new version of google ads, add open ads
 implementation 'com.github.otakuteam:adslib:v3.4'
