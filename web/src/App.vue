<template>
  <NavBar></NavBar>
  <router-view></router-view>
</template>

<script>
import NavBar from '@/components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
import $ from 'jquery';

export default{
  components: {
    NavBar
  },
  setup(){
    $.ajax({
      url: 'http://localhost:3000/user/account/token/',
      type: "post",
      data:{
        username: 'yxc',
        password: 'pyxc',
      } ,
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      }
    });

    $.ajax({
      url: "http://localhost:3000/user/account/info/",
      type: "get",
      headers:{
        Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODhmODM0NDM4MmU0OTdjODFlN2ZmMmM1ZWFlMzkxMiIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY3MTE3MjM2MiwiZXhwIjoxNjcyMzgxOTYyfQ.aSzV1O6sYztbQoYnHjmbjy8n6RhDKoijpUphMPTA66E",

      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      }
    });

    $.ajax({
      url: "http://localhost:3000/user/account/register/",
      type: "post",
      data: {
        username: "tsq",
        password: "ptsq",
        confirmedPassword: "ptsq",
      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      }
    })
  }
}
</script>

<style>
  body{
    background-image: url("@/assets/images/background.png");
    background-size: cover;
  }
</style>
