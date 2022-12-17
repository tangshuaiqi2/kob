<template>
    <ContentField>
        注册: 
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">

                        <label for="passowrd" class="form-label">密码</label>
                        <input v-model="password" type="passowrd" class="form-control" id="passowrd" placeholder="请输入密码">

                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="passowrd" class="form-control" id="confirmedPassword" placeholder="请确认密码">

                        <div class="error-message">{{error_message}}</div>
                        <button type="submit" class="btn btn-primary" @click="register">提交</button>
                    </div>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from "../../../components/ContentField.vue";
import { ref } from 'vue';
import router from '../../../router/index';
import $ from 'jquery';
 
export default {
    components: {
        ContentField,
    },
    setup(){
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_message = ref('');

        const register = () => {
            $.ajax({
            url: "http://localhost:3000/user/account/register/",
            type: "post",
            data: {
                username: username.value,
                password: password.value,
                confirmedPassword: confirmedPassword.value,
            },
            success(resp){
                if(resp.error_message === 'success'){
                    router.push({name: "user_account_login"});
                }
                else{
                    error_message.value = resp.error_message;
                }
            },
            error(resp){
                console.log(resp);
            }
            })
        }

        return{
            username,
            password,
            confirmedPassword,
            error_message,
            register,
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