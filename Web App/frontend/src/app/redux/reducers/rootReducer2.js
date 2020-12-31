import { combineReducers } from "redux";
import { firestoreReducer } from "redux-firestore";
import { firebaseReducer } from "react-redux-firebase";
import LoginReducer from "./LoginReducer";
import UserReducer from "./UserReducer";


const rootReducer = combineReducers({
  login: LoginReducer,
  firestore: firestoreReducer,
  firebase: firebaseReducer,
  user: UserReducer,
});

export default rootReducer;

// the key name will be the data property on the state object
