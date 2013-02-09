# Vision Glasses Management App

Android based application that will run a web-server on an Android device (phone, tablet, etc...) for other web browser to connect to. This allows a multitude of other devices that can connect to it: Windows, Mac, Linux, ChromeOS, iOS, Android, basically anything with a modern web browser.

### Requirements

* One Android Phone or Tablet
	- This will be the device that holds the data.
* One or more devices with a web browser
	- Ideally these would be something a larger screen (13" or larger), a keyboard and a mouse. This is to help make data input easier.

### Stuff used to make this
 * [Bootstrap](https://github.com/twitter/bootstrap) for the template
 * [backbone.js](https://github.com/documentcloud/backbone) for the web app
 * [require.js](https://github.com/jrburke/requirejs) for managing javascript modules
 * [Google Gson](https://code.google.com/p/google-gson/) for parsing json on the server side
 * [NanoHTTPd](https://github.com/elonen/nanohttpd) for the web server
