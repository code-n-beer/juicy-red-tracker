<template>
   <div class="login">
      <input type="text" placeholder="email" name="email">
      <input type="text" placeholder="password will be sent as plain text" name="password">
      <button name="submit"> Send </button>
    </div>
</template>

<script>
export default {
  name: 'User',
  data() {
    return {
      hello: 'ses!'
    };
  }
}
</script>

<style lang="sass">
  .login {
    background-color: red;
  }
</style>
