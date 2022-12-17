<template>
    <ContentField>
        我的Bot
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import $ from 'jquery'

export default {
    components: {
        ContentField
    },
    setup(){
        const jwt_token = localStorage.getItem("jwt_token");
        // $.ajax({
        //     url: "http://localhost:3000/user/bot/add/",
        //     type: "post",
        //     data:{
        //         title: "bot的标题",
        //         description: "bot description",
        //         content: "bot content",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + jwt_token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // })
        // $.ajax({
        //     url: "http://localhost:3000/user/bot/remove/",
        //     type: "post",
        //     data:{
        //         bot_id: 53,
        //     },
        //     headers: {
        //         Authorization: "Bearer " + jwt_token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // })
        // $.ajax({
        //     url: "http://localhost:3000/user/bot/update/",
        //     type: "post",
        //     data:{
        //         bot_id: 1,
        //         title: "bot的标题1",
        //         description: "bot description1",
        //         content: "bot content1",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + jwt_token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // })

        $.ajax({
            url: "http://localhost:3000/user/bot/getlist/",
            type: "get",
            headers: {
                Authorization: "Bearer " + jwt_token,
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

<sytle scoped>

</sytle>