<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script type="text/javascript">
    function createAccountForm() {
        console.log('createAccountForm() CALLED!!');

        let form = document.create_account_form;

        if (form.id.value == '') {
            alert('INPUT ADMIN ID.');
            form.id.focus();
        } else if (form.password.value == '') {
            alert('INPUT ADMIN PW.');
            form.password.focus();
        } else if (form.password_again.value == '') {
            alert('INPUT ADMIN PW AGAIN.');
            form.password_again.focus();
        } else if (form.password.value != form.password_again.value) {
            alert('Please check your password again.');
            form.password.focus();
        } else if (form.name.value == '') {
            alert('INPUT ADMIN NAME.');
            form.name.focus();
        } else if (form.gender.value == '') {
            alert('SELECET ADMIN GENDER.');
            form.gender.focus();
        } else if (form.part.value == '') {
            alert('INPUT ADMIN PART.');
            form.part.focus();
        } else if (form.position.value == '') {
            alert('INPUT ADMIN POSITION.');
            form.position.focus();
        } else if (form.email.value == '') {
            alert('INPUT ADMIN MAIL.');
            form.email.focus();
        } else if (form.phone.value == '') {
            alert('INPUT ADMIN PHONE.');
            form.phone.focus();
        } else {
            form.submit();
        }
    }
</script>
