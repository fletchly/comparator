import { getWellKnownIds } from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ parent }) => {
	const { conversations } = await parent();
	const wellKnown = await getWellKnownIds();
	return { conversations, wellKnown };
};
