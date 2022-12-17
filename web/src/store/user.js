import $ from 'jquery'

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true,
    },
    getters: {
    },
    mutations: {
        updateUser(state, user){
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token){
            state.token = token;
        },
        updateLogin(state, login){
            state.is_login = login;
        },

        logout(state){
            state.id = "";
            state.username= "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
            // state.pulling_info = false;
        },

        pulling_info(state,pulling_info){
            state.pulling_info = pulling_info;
        }
        
    },
    actions: {
        login(context, data){
            $.ajax({
                url: 'http://localhost:3000/user/account/token/',
                type: "post",
                data:{
                  username: data.username,
                  password: data.password,
                } ,
                success(resp){
                    if(resp.error_message === 'success'){
                        localStorage.setItem("jwt_token", resp.token);
                        console.log(resp);
                        context.commit("updateToken", resp.token);//如果想调用全局变量中的 mutations函数的话 用commit
                        data.success(resp);
                    }
                },
                error(resp){
                    data.error(resp);
                }
              });
        },

        getinfo(context, data){
            $.ajax({
                url: "http://localhost:3000/user/account/info/",
                type: "get",
                headers:{
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp){
                    if(resp.error_message === 'success'){
                        context.commit("updateUser", {
                            ...resp,
                            is_login: true,
                            });
                        data.success(resp);
                    }else{
                        data.error(resp);
                    }
                    
                },
                error(resp){
                    console.log(resp);
                }
            });
        },

        pulling_info(context, pulling_info){
            context.commit("pulling_info",pulling_info);
        },

        logout(context){
            localStorage.removeItem("jwt_token");
            context.commit("logout");
            location.reload();
        }
    },
    modules: {
    }
  }