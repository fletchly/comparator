import type { PageLoad } from './$types';

export const load: PageLoad = async ({ parent }) => {
	const { conversations } = await parent();
	return { conversations };
};
