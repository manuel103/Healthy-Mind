// import firebase from 'firebase/app'
// import 'firebase/firestore'
// import 'firebase/auth'

import * as firebase from "firebase"

const firebaseConfig = {
  apiKey: "AIzaSyC-3wSwrRkO2i3LzjHDOvWDToiaPw4krsY",
  authDomain: "dl4ddoa.firebaseapp.com",
  databaseURL: "https://dl4ddoa.firebaseio.com",
  projectId: "dl4ddoa",
  storageBucket: "dl4ddoa.appspot.com",
  messagingSenderId: "163619020443",
  appId: "1:163619020443:web:5f597d5ab8b864a28db03e",
};

export var fireDb = firebase.initializeApp(firebaseConfig)

export default fireDb.database().ref();
