<!--
  - This file is part of comparator, licensed under the Apache License 2.0
  -
  - Copyright (c) 2026 fletchly <https://github.com/fletchly>
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import { MessageSquare, House, MessageSquareCode, MessageSquareText } from '@lucide/svelte';
	import Sidebar, { type NavItem } from '$lib/components/ui/Sidebar.svelte';
	import { page } from '$app/state';

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
		}
	];

	// Flatten parents + children for route matching
	const allItems = navItems.flatMap((item) => [item, ...(item.children ?? [])]);

	let activeId = $derived(allItems.find((item) => page.url.pathname === item.href)?.id ?? '');

	let isMobile = $state(false);
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
	<title>Comparator Panel</title>
</svelte:head>

<div class="flex h-screen">
	<Sidebar items={navItems} bind:activeId bind:isMobile />

	<main
		class="flex-1 overflow-auto p-8"
		style:padding-left={isMobile ? 'calc(3.5rem + 1.5rem)' : undefined}
	>
		{@render children()}
	</main>
</div>
