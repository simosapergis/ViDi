# ViDi
This is an Android application for blind people which will automatically identify, translate and read text via Text-To-Speech API.
Specifically the application uses the following tools from Firebase Machine Learning (ML) Kit 
1.	Text identification
2.	Language identification
3.	Text translation

<h2>Purpose</h2>
<br>
<ul>
  <li>Objective is to help blind people with a translation tool that they can use without their interference</li>
  <li>ViDi is developed to translate text that can be captured from the camera of an Android device</li>
</ul>

<h2>How it works</h2>

<br>The phone can be used as a wearable (like a necklace) and the flow of the app is the following
<ul>
  <li>The camera is capturing images automatically every x seconds</li> 
  <li>For every image, searches for text using Vision API</li>
  <li>When text is found, tries to identify the language</li>
  <li>After language identification, attempts to translate the text</li>
  <li>The translated text is being converted to audio file, through Text-To-Speech Google API</li>
  <li>The audio file is being played by the media player</li>
</ul>
