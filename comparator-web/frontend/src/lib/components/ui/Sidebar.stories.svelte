<script module lang="ts">
	import { defineMeta } from '@storybook/addon-svelte-csf';
	import { type ComponentProps } from 'svelte';
	import { Home, Settings, Users, BarChart, FileText } from '@lucide/svelte';
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
		{ id: 'home', label: 'Home', href: '/' as const, icon: Home },
		{ id: 'conversation', label: 'Conversation', href: '/conversation' as const, icon: Users },
		{ id: 'chat', label: 'Chat', href: '/conversation/chat' as const, icon: BarChart },
		{ id: 'console', label: 'Console', href: '/conversation/console' as const, icon: FileText }
	];
</script>

<!--
  Mirrors +layout.svelte: sidebar is fixed on mobile so main needs pl-14 to avoid
  being obscured. On desktop the sidebar is in the flex flow so pl-0 applies.
-->
{#snippet template(args: Args)}
	<div class="relative flex h-screen">
		<Sidebar {...args as Args} />
		<main class="flex-1 overflow-auto p-6 pl-14 md:pl-6">
			<p class="text-sm text-gray-400">Main content area</p>
		</main>
	</div>
{/snippet}

<!-- Default: auto-collapses on mobile, expanded on desktop -->
<Story name="Default" args={{ items: defaultItems }} />

<!-- Active item pre-selected -->
<Story name="With Active Item" args={{ items: defaultItems, activeId: 'conversation' }} />

<!--
  Force-collapsed regardless of screen size.
  Useful for previewing the icon-only state on desktop.
-->
<Story name="Collapsed" args={{ items: defaultItems, collapsed: true }} />

<!-- Items without icons -->
<Story
	name="No Icons"
	args={{
		items: defaultItems.map(({ icon: _, ...item }) => item),
		activeId: 'home'
	}}
/>

<!-- Single item edge case -->
<Story
	name="Single Item"
	args={{
		items: [{ id: 'home', label: 'Home', href: '/' as const, icon: Home }],
		activeId: 'home'
	}}
/>
