<template>
    <ContentField>
        登录: 
        <div class="row justify-content-md-center" v-if="!$store.state.user.pulling_info">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">

                        <label for="passowrd" class="form-label">密码</label>
                        <input v-model="password" type="passowrd" class="form-control" id="passowrd" placeholder="请输入密码">

                        <div class="error-message">{{error_message}}</div>
                        <button type="submit" class="btn btn-primary">提交</button>
                    </div>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from "../../../components/ContentField.vue";
import {useStore} from 'vuex';
import {ref} from 'vue'
import router from '../../../router/index'

export default {
    components: {
        ContentField,
    },
    setup(){
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        const jwt_token = localStorage.getItem("jwt_token");
        if(jwt_token){
            store.commit("updateToken",jwt_token);
            store.dispatch("getinfo",{
                success(){
                    store.dispatch("pulling_info", true);
                    router.push({name: 'home_index'});
                },
                error(){
                    store.dispatch("pulling_info", true);
                }
            })
        }
        else{
            store.dispatch("pulling_info", false);
        }

        const login = () => {
            store.dispatch("login",{ //如果想调用全局变量store里面的action的函数的话 用dispatch
                username: username.value,
                password: password.value,
                
                success(){
                    store.dispatch("getinfo", {
                        success(){
                            
                            router.push({name: "home_index"});
                        }
                        
                    })
                    
                },

                error(){
                    error_message.value = "用户名或密码错误";
                    username.value = "";
                    password.value = "";
                },
            })
        }

        return{
            username,
            password,
            error_message,
            login,
        }
    }
}

</script>

<style scoped>

button{
    width: 100%;
}

div.error-message{
    color: red;
}

</style>