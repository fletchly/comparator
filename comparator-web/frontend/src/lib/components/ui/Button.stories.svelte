<script module>
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import Button from './Button.svelte';
	import { Trash, CheckCheck } from '@lucide/svelte';

	const { Story } = defineMeta({
		title: 'Ui/Button',
		component: Button,
		tags: ['autodocs']
	});
</script>

<Story name="Primary" args={{ variant: 'primary' }}>
	{#snippet children()}
		Click me
	{/snippet}
</Story>

<Story name="Success" args={{ variant: 'success' }}>
	{#snippet children()}
		<CheckCheck class="inline h-[1em] w-[1em] align-[-0.1em]" /> Submit
	{/snippet}
</Story>

<Story name="Destructive" args={{ variant: 'destructive' }}>
	{#snippet children()}
		<Trash class="inline h-[1em] w-[1em] align-[-0.1em]" /> Delete
	{/snippet}
</Story>

<Story name="Disabled" args={{ variant: 'primary', disabled: true }}>
	{#snippet children()}
		Disabled
	{/snippet}
</Story>
