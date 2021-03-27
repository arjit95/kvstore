package commands

import (
	"github.com/arjit95/kvstore/go/cmd/kvctl/factory"
	"github.com/spf13/cobra"
)

func CreatePutCommand(f *factory.Factory) *cobra.Command {
	return &cobra.Command{
		Use:                   "put",
		Short:                 "Adds a new key to server",
		Args:                  cobra.ExactArgs(2),
		DisableFlagsInUseLine: true,
		RunE: func(cmd *cobra.Command, args []string) error {
			key := args[0]
			val := []byte(args[1])

			for _, client := range f.Clients {
				err := client.Put(key, val)
				if err != nil {
					return err
				}
			}

			return nil
		},
	}
}
