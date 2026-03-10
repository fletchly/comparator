<script module>
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import ConversationListItem from './ConversationListItem.svelte';

	const { Story } = defineMeta({
		title: 'App/ConversationListItem',
		component: ConversationListItem,
		tags: ['autodocs']
	});
</script>

<Story
	name="Named"
	args={{
		id: 'abc-123',
		conversation: {
			displayName: 'Steve',
			messages: [
				{ type: 'user', name: 'Steve', content: 'hi' },
				{ type: 'assistant', content: 'Hello!', toolCalls: null }
			]
		}
	}}
/>

<Story
	name="Unnamed"
	args={{
		id: 'abc-123',
		conversation: { displayName: null, messages: [] }
	}}
/>

<Story
	name="Many Messages"
	args={{
		id: 'abc-123',
		conversation: {
			displayName: 'Alex',
			messages: Array.from({ length: 42 }, (_, i) => ({
				type: 'user',
				name: 'Alex',
				content: `Message ${i}`
			}))
		}
	}}
/>
