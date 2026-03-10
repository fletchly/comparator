<script module>
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import ToolCallBadge from './ToolCallBadge.svelte';

	const { Story } = defineMeta({
		title: 'App/ToolCallBadge',
		component: ToolCallBadge,
		tags: ['autodocs']
	});
</script>

<Story
	name="Collapsed"
	args={{
		toolCall: { name: 'give_item', arguments: { player: 'Steve', item: 'diamond', amount: 64 } },
		open: false
	}}
/>

<Story
	name="Expanded"
	args={{
		toolCall: { name: 'give_item', arguments: { player: 'Steve', item: 'diamond', amount: 64 } },
		open: true
	}}
/>

<Story
	name="No Args"
	args={{
		toolCall: { name: 'list_players', arguments: {} },
		open: true
	}}
/>
