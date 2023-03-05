# Internet-Speed-Test
Ookla servers based android internet speed test (open source complete project)

|   |   |
| ------------ | ------------ |
| <img src="https://github.com/farimarwat/Internet-Speed-Test/blob/master/images/03.png" width="70%" height="70%"/>  |  <img src="https://github.com/farimarwat/Internet-Speed-Test/blob/master/images/04.png" width="70%" height="70%"/> |

**<a href="https://github.com/farimarwat/Internet-Speed-Test/tree/master/images" >More Screen Shots</a>**

**Features**
- Ookla servers
- Download speed
- Upload speed
- Misc: ping, jitter etc
- MVVM architecture
- Room Database
- Admob (Native/Legacy) ready
- Firebase analytics and crashlatics implemented
- Google map
- Fully tested on robotest

Android Internet Speed Test is an open source project that provides a robust and reliable internal speed test API based on the popular Ookla Speed Test servers. With this API, developers can easily integrate internet speed test functionality into their Android applications, providing users with an accurate and comprehensive analysis of their network performance.

One of the key features of this project is its use of the Builder design pattern for the speed test API. This pattern allows developers to construct complex objects step by step, giving them greater control over the creation process and ensuring that all necessary components are included. This makes the API more flexible and easier to use, while also reducing the likelihood of errors or inconsistencies.

The Android Internet Speed Test project is built with a focus on scalability and maintainability, using best practices and industry standards to ensure high performance and reliability. It is fully open source, so developers can contribute to the project and use it in their own applications free of charge.

If you're looking for a powerful and customizable internet speed test solution for your Android app, look no further than Android Internet Speed Test. With its robust API and intuitive design, it's the perfect choice for developers who want to provide their users with the best possible experience.


**Note: This project is Admob and Firebase services ready including google  map**
### 1 Usage
Clone the project:

`git clone https://github.com/farimarwat/Internet-Speed-Test.git`

### 2 Modify local.properties file:

    ADMOB_API_KEY=ca-app-pub-1234510540123456~1234532400
    GOOGLEMAP_API_KEY=AIzasdfkjghfj-1234jfksl552PqAhHem_12345
    ADMOB_NATIVE_ADD=ca-app-pub-122912349094032/1234543219
    ADMOB_INTERSTITIAL_ADD=ca-app-pub-8029123454321239/2343290943

**These keys are dummy keys. so change only keys**
### 3 Put your firebase json file in app root dir
and you are done.


**In case if you want to use only the Speed Test API then it is as simple as abc**
1. Import the "speedtest" module in your project
2. Include it as dependency and done.

### Speed Test API
This is a library included in this project to test speed. You can use other server/urls to test upload and dowload speed instead of ookla servers both free and premium


#### TestDownloader Builder


     mBuilderDownload = TestDownloader.Builder(url)
                .addListener(object : TestDownloader.TestDownloadListener {
                    override fun onStart() {
                       
                    }
    
                    override fun onProgress(progress: Double, elapsedTimeMillis: Double) {
                   
                    }
    
                    override fun onFinished(
                        finalprogress: Double,
                        datausedinkb: Int,
                        elapsedTimeMillis: Double
                    ) {
                       
                    }
    
                    override fun onError(msg: String) {
                       
                    }
    
                })
                .setTimeOUt(mTimeOut)
                .setThreadsCount(mConnectionType)
                .build()
            mBuilderDownload?.start()

#### TestUploader Builder

    mBuilderUpload = TestUploader.Builder(fullurl)
                .addListener(object : TestUploader.TestUploadListener {
                    override fun onStart() {
                      
                    }
    
                    override fun onProgress(progress: Double, elapsedTimeMillis: Double) {
                      
                    }
                    override fun onFinished(
                        finalprogress: Double,
                        datausedinkb: Int,
                        elapsedTimeMillis: Double
                    ) {
                        
                    }
    
                    override fun onError(msg: String) {
                      
                    }
    
                })
                .setTimeOUt(mTimeOut)
                .setThreadsCount(mConnectionType)
                .build()
            mBuilderUpload?.start()



