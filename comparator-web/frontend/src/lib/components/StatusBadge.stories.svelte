<script module>
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import StatusBadge from './StatusBadge.svelte';

	const { Story } = defineMeta({
		title: 'App/StatusBadge',
		component: StatusBadge,
		tags: ['autodocs']
	});
</script>

<Story name="Running" args={{ status: 'running' }} />

<Story name="Stopped" args={{ status: 'stopped' }} />

<Story name="Error" args={{ status: 'error' }} />

<Story name="Unknown" args={{ status: 'unknown' }} />

<Story name="Custom Label" args={{ status: 'running', label: 'Web panel active on :8080' }} />
