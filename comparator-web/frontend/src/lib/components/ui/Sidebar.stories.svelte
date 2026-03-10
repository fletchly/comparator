<script module lang="ts">
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import { type ComponentProps } from 'svelte';
	import {
		Users,
		House,
		MessageSquare,
		MessageSquareText,
		MessageSquareCode
	} from '@lucide/svelte';
	import Sidebar from './Sidebar.svelte';

	type Args = ComponentProps<typeof Sidebar>;

	const { Story } = defineMeta({
		title: 'Navigation/Sidebar',
		component: Sidebar,
		render: template,
		parameters: {
			layout: 'fullscreen',
			sveltekit_experimental: {
				hrefs: {
					'/.*': {
						callback: (to: string, event: MouseEvent) => {
							console.log('navigate', to, event);
						},
						asRegex: true
					}
				}
			}
		}
	});

	const defaultItems: Args['items'] = [
		{ id: 'home', label: 'Home', href: '/' as const, icon: House },
		{
			id: 'conversation',
			label: 'Conversation',
			href: '/conversation' as const,
			icon: MessageSquare,
			children: [
				{ id: 'chat', label: 'Chat', href: '/conversation/chat' as const, icon: MessageSquareText },
				{
					id: 'console',
					label: 'Console',
					href: '/conversation/console' as const,
					icon: MessageSquareCode
				}
			]
		}
	];
</script>

{#snippet template(args: Args)}
	{@const state = { isMobile: false }}
	<div class="relative flex h-screen">
		<Sidebar {...args as Args} bind:isMobile={state.isMobile} />
		<main
			class="flex-1 overflow-auto p-6"
			style:padding-left={state.isMobile ? '3.5rem' : undefined}
		>
			<p class="text-sm text-gray-400">Main content area</p>
		</main>
	</div>
{/snippet}

<!-- Default: no active item, children hidden -->
<Story name="Default" args={{ items: defaultItems }} />

<!-- Parent active: children visible -->
<Story name="Parent Active" args={{ items: defaultItems, activeId: 'conversation' }} />

<!-- Child active: parent stays expanded -->
<Story name="Child Active" args={{ items: defaultItems, activeId: 'chat' }} />

<!-- Collapsed: children never shown -->
<Story name="Collapsed" args={{ items: defaultItems, collapsed: true }} />

<!-- Collapsed with active child: children hidden, parent icon shown -->
<Story
	name="Collapsed + Child Active"
	args={{ items: defaultItems, activeId: 'chat', collapsed: true }}
/>

<!-- No icons -->
<Story
	name="No Icons"
	args={{
		items: defaultItems.map(({ icon: _, ...item }) => item),
		activeId: 'conversation'
	}}
/>

<!-- Single item, no children -->
<Story
	name="Single Item"
	args={{
		items: [{ id: 'home', label: 'Home', href: '/' as const, icon: House }],
		activeId: 'home'
	}}
/>
