<script module>
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import Message from './Message.svelte';

	const { Story } = defineMeta({
		title: 'App/Message',
		component: Message,
		tags: ['autodocs']
	});
</script>

<Story name="User" args={{ message: { type: 'user', name: 'Steve', content: 'Hello, world!' } }} />

<Story
	name="Assistant"
	args={{ message: { type: 'assistant', content: 'I can help you with that.', toolCalls: null } }}
/>

<Story
	name="Assistant with Tool Calls"
	args={{
		message: {
			type: 'assistant',
			content: '',
			toolCalls: [
				{ name: 'give_item', arguments: { player: 'Steve', item: 'diamond', amount: 64 } },
				{ name: 'broadcast', arguments: { message: 'Hello everyone!' } }
			]
		}
	}}
/>

<Story
	name="Tool"
	args={{ message: { type: 'tool', name: 'give_item', content: 'Gave 64 diamond(s) to Steve.' } }}
/>
