import { createStore } from 'vuex'
import ModuleUser from './user'
import MudulePk from './pk'

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user: ModuleUser, //用这个就可以看着美观 相当于包装了一个Store放过来 就不用全部都放到一个Store里面了
    pk: MudulePk,
  }
})
