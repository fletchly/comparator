<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import { MessageSquare, House, MessageSquareCode, MessageSquareText, Wrench, Play } from '@lucide/svelte';
	import Sidebar, { type NavItem } from '$lib/components/ui/Sidebar.svelte';
	import { page } from '$app/state';
	import { getVersion } from '$lib/api';
	import { onMount } from 'svelte';
	import { toolRuns } from '$lib/stores/toolRuns';
	import { invalidate } from '$app/navigation';

	const { children } = $props();

	const navItems: NavItem[] = [
		{ id: 'home', label: 'Home', href: '/', icon: House },
		{
			id: 'conversation',
			label: 'Conversations',
			href: '/conversation',
			icon: MessageSquare,
			children: [
				{ id: 'chat', label: 'Chat', href: '/conversation/chat', icon: MessageSquareText },
				{ id: 'console', label: 'Console', href: '/conversation/console', icon: MessageSquareCode }
			]
		},
		{ id: 'tool', label: 'Tool Runs', href: '/tool', icon: Play },
		{ id: 'tool-registered', label: 'Registered Tools', href: '/tool/registered', icon: Wrench }
	];

	const allItems = navItems.flatMap((item) => [item, ...(item.children ?? [])]);
	let activeId = $derived(allItems.find((item) => page.url.pathname === item.href)?.id ?? '');
	let isMobile = $state(false);

	let version = $state<string | null>(null);
	let connected = $state(false);

	onMount(() => {
		getVersion()
				.then((v) => (version = v.version))
				.catch(() => (version = null));

		const source = new EventSource('/api/events');
		source.onopen = () => (connected = true);
		source.onerror = () => (connected = false);

		source.addEventListener('tool-executed', (event) => {
			const payload = JSON.parse(event.data);
			const id = crypto.randomUUID?.() ?? Math.random().toString(36).slice(2);
			toolRuns.update((runs) => [...runs, { ...payload, id, timestamp: new Date() }]);
		});

		// Invalidate conversation-dependent load functions on any conversation event.
		// This covers the conversation list, individual conversation pages, and the
		// dashboard counts — all of which call depends('app:conversations').
		source.addEventListener('message', () => invalidate('app:conversations'));
		source.addEventListener('cleared', () => invalidate('app:conversations'));
		source.addEventListener('all-cleared', () => invalidate('app:conversations'));

		return () => source.close();
	});
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
	<title>Comparator Panel</title>
</svelte:head>

<div class="flex h-screen">
	<Sidebar items={navItems} bind:activeId bind:isMobile />

	<div class="flex flex-1 flex-col overflow-hidden">
		<header
				class="flex items-center justify-between border-b border-b-muted bg-background-secondary px-6 py-2"
				style:padding-left={isMobile ? 'calc(3.5rem + 1.5rem)' : undefined}
		>
			<span class="font-mono text-xs text-muted-light uppercase">
				{version ?? '—'}
			</span>
			<span
					data-connected={connected}
					class="flex items-center gap-1.5 font-mono text-xs data-[connected=false]:text-destructive data-[connected=true]:text-success"
			>
				<span class="size-1.5 rounded-full bg-current"></span>
				{connected ? 'online' : 'offline'}
			</span>
		</header>

		<main
				class="flex-1 overflow-auto p-8"
				style:padding-left={isMobile ? 'calc(3.5rem + 1.5rem)' : undefined}
		>
			{@render children()}
		</main>

		<footer
				class="flex items-center gap-4 border-t border-t-muted bg-background-secondary px-6 py-2 font-mono text-xs text-muted-light"
				style:padding-left={isMobile ? 'calc(3.5rem + 1.5rem)' : undefined}
		>
			<a
					href="https://github.com/fletchly/comparator"
					target="_blank"
					rel="noopener noreferrer"
					class="transition-colors hover:text-foreground"
			>
				github
			</a>
			<a
					href="https://fletchly.gitbook.io/comparator-docs/"
					target="_blank"
					rel="noopener noreferrer"
					class="transition-colors hover:text-foreground"
			>
				docs
			</a>
		</footer>
	</div>
</div>