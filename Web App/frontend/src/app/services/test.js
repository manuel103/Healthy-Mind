import React, {useEffect, useState} from 'react'

function Test() {
      const [doctors, setDoctors] = useState([]);

      // export function userDetails () {

      useEffect(() => {
        async function loadDoctors() {
          //fetch the patient list
          const request = await fetch("http://localhost:9000/api/getDoctors", {
            //use the authorization https://www.youtube.com/watch?v=T3Px88x_PsA http://localhost:3000/#/app/main/dashboard
            // headers: {
            //   Authorization: "Bearer " + localStorage.getItem("@token"),
            // },
          });

          const allDoctors = await request.json();

          console.log("Doctors", allDoctors);

          setDoctors(allDoctors.doctors);
        }

        //invoke the function
        loadDoctors();
      }, []);

    return (
        <div>
            
        </div>
    )
}

export default Test
