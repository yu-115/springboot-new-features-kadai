const stripe = Stripe('pk_test_51QXnln08B8D45f992wcmllQ9f7d9N38yAyLlhBK5gPodcjaJHO9SYO81TWtfncUQlwbGT3Gj4cSqlruygGYQUw9v00k0pMH3Rw');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
	stripe.redirectToCheckout({
		sessionId: sessionId
	})
});