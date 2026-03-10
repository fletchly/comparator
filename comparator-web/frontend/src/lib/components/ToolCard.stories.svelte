<script module>
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import ToolCard from './ToolCard.svelte';

	const { Story } = defineMeta({
		title: 'App/ToolCard',
		component: ToolCard,
		tags: ['autodocs']
	});
</script>

<Story
	name="No Parameters"
	args={{
		tool: {
			name: 'list_players',
			description: 'Returns a list of all currently online players.',
			parameters: []
		}
	}}
/>

<Story
	name="With Parameters"
	args={{
		tool: {
			name: 'give_item',
			description: 'Gives an item to a player.',
			parameters: [
				{
					name: 'player',
					type: 'string',
					description: 'The target player name.',
					required: true,
					enum: null,
					'element-type': null
				},
				{
					name: 'item',
					type: 'string',
					description: 'The item ID to give.',
					required: true,
					enum: ['diamond', 'gold_ingot', 'iron_ingot'],
					'element-type': null
				},
				{
					name: 'amount',
					type: 'integer',
					description: 'How many items to give.',
					required: false,
					enum: null,
					'element-type': null
				}
			]
		}
	}}
/>

<Story
	name="Array Parameter"
	args={{
		tool: {
			name: 'broadcast_list',
			description: 'Broadcasts a list of messages.',
			parameters: [
				{
					name: 'messages',
					type: 'array',
					description: 'List of messages to broadcast.',
					required: true,
					enum: null,
					'element-type': 'string'
				}
			]
		}
	}}
/>
