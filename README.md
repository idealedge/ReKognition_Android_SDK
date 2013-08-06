Current Version: 1.0
===============================

The folder contains our ReKognition Android SDKs (RekoSDK.java under folder named SDK). 



For more information about our ReKognition API, please read our 
<a href="http://v2.rekognition.com/developer/docs">documentation</a>.


This Java SDK is intent for develops who wants to integrate Rekognition API into their 
Android application. Each public function in this class represents an API call.
Every time you call a function, it will send API request and 
wait for response in another thread, and call the callback function your passed in 
once the response is received.
Usage:
  <ol>
  <li>  1. Update the value of sAPI_KEY and sAPI_SECRET to your API key and API secret. </li>
  <pre><code>
  static private String sAPI_KEY = "YourAPIKEY";
	static private String sAPI_SECRET = "YourAPISecret";
  </code></pre>
  <li>  2. Create a callback object with the callback function. </li>
     For example: 
     <pre><code>
     RekoSDK.APICallback callback = new RekoSDK.APICallback(){
		public void gotResponse(String sResponse){
			print(sResponse);
		}
	};
    </code></pre>
  <li>  3. Call it in your code as simple as </li>
     Reko.face_train(callback);
     and You're done!
  </ol>

Examples:
   Under Example folder, we have a demo Android project that contains very basic example of how 
to use the RekoSDK.

   The demo provide two ways to interact with API, choose a image from your local gallery and call our 
face_detect API, or input a image url and call our face_detect API.   

For any questions, please contact eng@orbe.us
